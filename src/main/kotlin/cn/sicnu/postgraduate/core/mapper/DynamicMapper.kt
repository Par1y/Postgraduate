package cn.sicnu.postgraduate.core.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import cn.sicnu.postgraduate.core.entity.Dynamic
import org.apache.ibatis.annotations.Mapper

@Mapper
public interface DynamicMapper : BaseMapper<Dynamic> {
    
}