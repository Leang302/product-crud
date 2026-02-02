package com.example.productscrud.service.impl;


import com.example.productscrud.model.entity.AppUser;
import com.example.productscrud.model.request.AppUserRequest;
import com.example.productscrud.model.response.AppUserResponse;
import com.example.productscrud.repository.AppUserRepository;
import com.example.productscrud.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.getUserByEmail(email);
    }
    @Override
    public AppUserResponse register(AppUserRequest request) {
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        AppUser appUser = appUserRepository.save(request.toEntity());
       /* for (String role : request.getRoles()){
            if (role.equals("ROLE_USER")){
                appUserRepository.insertUserIdAndRoleId(1L, appUser.getUserId());
            }
            if (role.equals("ROLE_ADMIN")){
                appUserRepository.insertUserIdAndRoleId(2L, appUser.getUserId());
            }
        }*/
        return modelMapper.map(appUserRepository.findById(appUser.getUserId()), AppUserResponse.class);
    }
}
