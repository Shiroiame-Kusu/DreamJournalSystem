package icu.nyat.dreamjournalsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import icu.nyat.dreamjournalsystem.entity.Dream;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 梦境 Mapper
 */
@Mapper
public interface DreamMapper extends BaseMapper<Dream> {

    /**
     * 分页查询用户的梦境列表
     */
    IPage<Dream> selectPageByUserId(Page<Dream> page, 
                                     @Param("userId") Long userId,
                                     @Param("keyword") String keyword,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate,
                                     @Param("dreamType") Dream.DreamType dreamType,
                                     @Param("isFavorite") Boolean isFavorite);

    /**
     * 统计用户梦境数量
     */
    @Select("SELECT COUNT(*) FROM dreams WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);

    /**
     * 统计用户收藏梦境数量
     */
    @Select("SELECT COUNT(*) FROM dreams WHERE user_id = #{userId} AND is_favorite = true")
    int countFavoritesByUserId(@Param("userId") Long userId);

    /**
     * 统计指定月份的梦境数量
     */
    @Select("SELECT COUNT(*) FROM dreams WHERE user_id = #{userId} AND YEAR(dream_date) = #{year} AND MONTH(dream_date) = #{month}")
    int countByUserIdAndMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    /**
     * 获取用户最近的梦境
     */
    @Select("SELECT * FROM dreams WHERE user_id = #{userId} ORDER BY dream_date DESC, created_at DESC LIMIT #{limit}")
    List<Dream> findRecentByUserId(@Param("userId") Long userId, @Param("limit") int limit);
}
