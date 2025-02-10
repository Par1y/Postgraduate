package cn.sicnu.postgraduate.core.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import cn.sicnu.postgraduate.core.entity.Plan
import org.apache.ibatis.annotations.Mapper

@Mapper
public interface PlanMapper : BaseMapper<Plan> {
    
}