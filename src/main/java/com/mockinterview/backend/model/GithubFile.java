package com.mockinterview.backend.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GithubFile {
    public String name;
    public String path;
    public String type;
    public String download_url;
    public String url;
}
