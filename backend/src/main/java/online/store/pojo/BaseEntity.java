package online.store.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体类的基类
 * 包含各个表的公共字段
 * 有的表中没有全部字段，忽略不使用
 */
@Data
public class BaseEntity implements Serializable {

    /**
     * 每个表中的主键 id
     */
    @TableId
    private Long id;

    /**
     * 各个表中每条记录创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 各个表中每条记录最后修改时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
