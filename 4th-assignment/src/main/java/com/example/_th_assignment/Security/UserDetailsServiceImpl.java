package com.example._th_assignment.Security;

import com.example._th_assignment.Dto.UserDto;
import com.example._th_assignment.Entity.User;
import com.example._th_assignment.JpaRepository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserJpaRepository userJpaRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userJpaRepository.findByEmailAndIsdeletedFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("UnAuthorized with email: " + username));

        UserDto userDto = user.toUserDto();

        //원래는 여러 권한이 들어갈수 잇음
        //근데 권한 설정을 따로 하지 않으니 user만 넣고 list로 감쌈
        List<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_USER"));


        return new CustomUserDetails(userDto, authorities);
    }
}
