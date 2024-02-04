package com.dmj.web.common;

import java.io.Serializable;
import lombok.Data;

/**
 * 删除请求
 *
 * @author  dmj
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}