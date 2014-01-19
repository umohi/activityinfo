package org.activityinfo.geo.rtree;

import com.google.appengine.labs.repackaged.com.google.common.collect.Iterables;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A page (or node) within the R tree. Pages can either
 * be leaf pages, which store pointers to actual resources
 * or they can be non-leaves, which store references to child
 * pages.
 *
 */
public class Page extends Bounded {

    public static final byte PAGE_ENTRY = 1;
    public static final byte RESOURCE_ENTRY = 2;


    public static class Entry extends Bounded {
        private byte type;
        private long pageId;
        private String dataUri;

        public long getPageId() {
            return pageId;
        }

        public String getDataUri() {
            return dataUri;
        }

        private void write(DataOutput out) throws IOException {
            out.writeByte(type);
            if(type == PAGE_ENTRY) {
                out.writeLong(pageId);
            } else {
                out.writeUTF(dataUri);
            }
            writeBounds(out);
        }

        private static Entry read(DataInput in) throws IOException {
            Entry newEntry = new Entry();
            newEntry.type = in.readByte();
            if(newEntry.type == PAGE_ENTRY) {
                newEntry.pageId = in.readLong();
            } else {
                newEntry.dataUri = in.readUTF();
            }
            newEntry.readBounds(in);
            return newEntry;
        }
    }

    private final long id;
    private boolean leaf = true;

    private List<Entry> entries = Lists.newArrayList();

    private Page(long id) {
        this.id = id;
    }

    /**
     *
     * @return the page's id
     */
    public long getId() {
        return id;
    }

    public static Entry newDataEntry(String uri, Bounded rect) {
        Entry entry = new Entry();
        entry.type = RESOURCE_ENTRY;
        entry.dataUri = uri;
        entry.expandTo(rect);
        return entry;
    }

    public static Entry newEntry(Page page) {
        Entry entry = new Entry();
        entry.type = PAGE_ENTRY;
        entry.pageId = page.getId();
        entry.expandTo(page);
        return entry;
    }

    public static Page emptyPage(long pageId) {
        return new Page(pageId);
    }

    public Collection<Entry> getEntries() {
        return Collections.unmodifiableCollection(entries);
    }

    public int getEntryCount() {
        return entries.size();
    }

    public boolean isLeaf() {
        return leaf;
    }

    /**
     * @return true if this page contains a reference to
     * the resource identified by the uri.
     */
    public boolean containsData(String dataUri) {

        Preconditions.checkNotNull(dataUri);
        for(Entry entry : entries) {
            if(dataUri.equals(entry.dataUri)) {
                return true;
            }
        }
        return false;
    }

    public void addEntry(Page page) {
        addEntry(Page.newEntry(page));
    }


    public void addEntries(Iterable<Entry> entries) {
        for(Entry entry : entries) {
            addEntry(entry);
        }
    }

    public void addEntry(Entry e) {
        if(entries.isEmpty()) {
            if(e.type == PAGE_ENTRY) {
                leaf = false;
            }
        } else if(leaf) {
            assert e.type == RESOURCE_ENTRY;
        } else {
            assert e.type == PAGE_ENTRY;
        }
        entries.add(e);
        expandTo(e);
    }

    public byte[] serialize() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bos);

            // write entries
            out.writeInt(entries.size());
            for(Entry entry : entries) {
                entry.write(out);
            }
            out.flush();
            return bos.toByteArray();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Page deserialize(long id, byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream in = new DataInputStream(bais);

        Page page = new Page(id);

        int numEntries = in.readInt();
        for(int i=0;i!=numEntries;++i) {
            page.addEntry(Entry.read(in));
        }
        return page;
    }
}
