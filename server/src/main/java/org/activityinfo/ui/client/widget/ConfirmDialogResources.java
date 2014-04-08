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
    private ConfirmDialogMessages confirm;
    @Nonnull
    private ConfirmDialogMessages progress;
    @Nonnull
    private ConfirmDialogMessages failed;

    public ConfirmDialogResources(@Nonnull ConfirmDialogMessages confirm, @Nonnull ConfirmDialogMessages progress, @Nonnull ConfirmDialogMessages failed) {
        this.confirm = confirm;
        this.progress = progress;
        this.failed = failed;
    }

    @Nonnull
    public ConfirmDialogMessages getConfirm() {
        return confirm;
    }

    public void setConfirm(@Nonnull ConfirmDialogMessages confirm) {
        this.confirm = confirm;
    }

    @Nonnull
    public ConfirmDialogMessages getProgress() {
        return progress;
    }

    public void setProgress(@Nonnull ConfirmDialogMessages progress) {
        this.progress = progress;
    }

    @Nonnull
    public ConfirmDialogMessages getFailed() {
        return failed;
    }

    public void setFailed(@Nonnull ConfirmDialogMessages failed) {
        this.failed = failed;
    }
}
