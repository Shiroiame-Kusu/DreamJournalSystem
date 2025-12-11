package icu.nyat.dreamjournalsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import icu.nyat.dreamjournalsystem.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查找用户
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    /**
     * 根据邮箱查找用户
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(@Param("email") String email);

    /**
     * 检查用户名是否存在
     */
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    int countByUsername(@Param("username") String username);

    /**
     * 检查邮箱是否存在
     */
    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    int countByEmail(@Param("email") String email);

    /**
     * 更新登录信息
     */
    @Update("UPDATE users SET last_login_at = NOW(), last_login_ip = #{ip}, login_attempts = 0 WHERE id = #{userId}")
    int updateLoginInfo(@Param("userId") Long userId, @Param("ip") String ip);

    /**
     * 增加登录失败次数
     */
    @Update("UPDATE users SET login_attempts = login_attempts + 1 WHERE id = #{userId}")
    int incrementLoginAttempts(@Param("userId") Long userId);

    /**
     * 锁定账户
     */
    @Update("UPDATE users SET locked_until = #{lockedUntil} WHERE id = #{userId}")
    int lockAccount(@Param("userId") Long userId, @Param("lockedUntil") java.time.LocalDateTime lockedUntil);
}
