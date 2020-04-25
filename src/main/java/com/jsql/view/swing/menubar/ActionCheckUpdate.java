package com.jsql.view.swing.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.jsql.util.GitUtil.ShowOnConsole;
import com.jsql.view.swing.util.MediatorHelper;

public class ActionCheckUpdate implements ActionListener, Runnable {

    @Override
    public void run() {
        
        MediatorHelper.model().getMediatorUtils().getGitUtil().checkUpdate(ShowOnConsole.YES);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        new Thread(this, "ThreadCheckUpdate").start();
    }
}
