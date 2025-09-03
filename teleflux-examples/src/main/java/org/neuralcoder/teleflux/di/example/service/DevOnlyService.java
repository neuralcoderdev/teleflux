package org.neuralcoder.teleflux.di.example.service;


import org.neuralcoder.teleflux.di.api.annotations.Profile;
import org.neuralcoder.teleflux.di.api.annotations.Service;

@Service
@Profile({"dev"})
public class DevOnlyService {
    public String msg() { return "[DEV] active"; }
}
