package org.activityinfo.ui.client.widget;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import javax.annotation.Nonnull;

/**
 * @author yuriyz on 4/7/14.
 */
public class ConfirmDialogResources {

    @Nonnull
    private String confirmTitle;
    @Nonnull
    private String progressTitle;
    @Nonnull
    private String failedTitle;

    @Nonnull
    private String confirmMessage;
    @Nonnull
    private String progressMessage;
    @Nonnull
    private String failedMessage;

    @Nonnull
    private String confirmOkButtonText;
    @Nonnull
    private String progressOkButtonText;
    @Nonnull
    private String failedOkButtonText;

    public ConfirmDialogResources() {
    }

    public String getConfirmTitle() {
        return confirmTitle;
    }

    public void setConfirmTitle(String confirmTitle) {
        this.confirmTitle = confirmTitle;
    }

    public String getProgressTitle() {
        return progressTitle;
    }

    public void setProgressTitle(String progressTitle) {
        this.progressTitle = progressTitle;
    }

    public String getFailedTitle() {
        return failedTitle;
    }

    public void setFailedTitle(String failedTitle) {
        this.failedTitle = failedTitle;
    }

    public String getConfirmMessage() {
        return confirmMessage;
    }

    public void setConfirmMessage(String confirmMessage) {
        this.confirmMessage = confirmMessage;
    }

    public String getProgressMessage() {
        return progressMessage;
    }

    public void setProgressMessage(String progressMessage) {
        this.progressMessage = progressMessage;
    }

    public String getFailedMessage() {
        return failedMessage;
    }

    public void setFailedMessage(String failedMessage) {
        this.failedMessage = failedMessage;
    }

    public String getConfirmOkButtonText() {
        return confirmOkButtonText;
    }

    public void setConfirmOkButtonText(String confirmOkButtonText) {
        this.confirmOkButtonText = confirmOkButtonText;
    }

    public String getProgressOkButtonText() {
        return progressOkButtonText;
    }

    public void setProgressOkButtonText(String progressOkButtonText) {
        this.progressOkButtonText = progressOkButtonText;
    }

    public String getFailedOkButtonText() {
        return failedOkButtonText;
    }

    public void setFailedOkButtonText(String failedOkButtonText) {
        this.failedOkButtonText = failedOkButtonText;
    }
}
