
// The idea is to implement at least part of the indexedb store in plain JavaScript
// as this api is still quite fluid and testing it in GWT code is very painful.


function IndexedDbStore() {

    var store = this;
    var latestVersion = 2;

    this.indexedDB = window.indexedDB || window.mozIndexedDB || window.webkitIndexedDB || window.msIndexedDB;
    this.IDBTransaction = window.IDBTransaction || window.webkitIDBTransaction || window.msIDBTransaction;
    this.IDBKeyRange = window.IDBKeyRange || window.webkitIDBKeyRange || window.msIDBKeyRange;
    // (Mozilla has never prefixed these objects, so we don't need window.mozIDB*)

    this.init = function(name) {
        var request = store.indexedDB.open(name, latestVersion);
        request.onerror = function(event) {
            store.initError = 1;
        };
        request.onsuccess = function(event) {
            console.log("open db succeeded");
            store.db = request.result;

            if (store.db.version !== latestVersion) {
                // old version of indexeddb
                console.log("going to change version");

                var versionChangeRequest = store.db.setVersion(2);

                //versionChangeRequest.onblocked = function(event) {...}
                versionChangeRequest.onsuccess = function(event) {
                    var versionChangeTransaction = event.target.result;
                    store.upgrade();
                    //versionChangeTransaction.oncomplete = function(event) {...}
                };

            }
        };
        request.onupgradeneeded = function(event) {
            store.db = event.target.result;
            store.upgrade();
        };

    };

    this.upgrade = function() {

        var instanceStore = store.db.createObjectStore("instances", { keyPath: "id" });
        instanceStore.createIndex('class', 'class', { unique: false });
        instanceStore.createIndex('parent', 'parent', { unique: false });

        var snapshotStore = store.db.createObjectStore("snapshots", { keyPath: "id" });
    };

    this.persist = function(instance, onSuccess, onFailure) {
        var transaction = store.db.transaction(["instances"], "readwrite");
        transaction.oncomplete = function(event) {
            if(onSuccess) onSuccess();
        };

        transaction.onerror = function(event) {
            if(onFailure) onFailure();
        };

        var objectStore = transaction.objectStore("instances");
        objectStore.add(instance);
    };

    return this;
}



