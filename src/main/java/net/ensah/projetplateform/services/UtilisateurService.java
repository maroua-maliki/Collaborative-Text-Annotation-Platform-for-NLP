package net.ensah.projetplateform.services;

import jakarta.transaction.Transactional;
import net.ensah.projetplateform.entities.Utilisateur;
import net.ensah.projetplateform.repository.UtilisateurRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurService implements UserDetailsService {
    private final UtilisateurRepository utilisateurRepository;
    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByLogin(login);
        if(utilisateur == null){
            throw new UsernameNotFoundException("L'utilisateur n'existe pas");
        }
        GrantedAuthority authority = new SimpleGrantedAuthority(utilisateur.getRole().getNomRole());
        return User
                .withUsername(login)
                .authorities(authority)
                .password(utilisateur.getPassword())
                .build();
    }
}