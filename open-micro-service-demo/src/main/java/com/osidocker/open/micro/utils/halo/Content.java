package com.osidocker.open.micro.utils.halo;

import lombok.Data;

@Data
class Content {
    private String snapshotName;
    private String raw;
    private String content;
    private String rawType = "markdown";
}
