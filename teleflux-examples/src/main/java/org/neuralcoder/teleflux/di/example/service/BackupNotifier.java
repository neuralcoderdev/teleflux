package org.neuralcoder.teleflux.di.example.service;

import org.neuralcoder.teleflux.di.api.annotations.Service;

@Service("backup")
public class BackupNotifier implements Notifier {

    @Override public String notify(String payload) { return "[BACKUP] " + payload; }
}
