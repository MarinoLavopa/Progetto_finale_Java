package it.aulab.progetto_finale_java.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.aulab.progetto_finale_java.models.CareerRequest;
import it.aulab.progetto_finale_java.models.Role;
import it.aulab.progetto_finale_java.models.User;
import it.aulab.progetto_finale_java.repositories.CareerRequestRepository;
import it.aulab.progetto_finale_java.repositories.RoleRepository;
import it.aulab.progetto_finale_java.repositories.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class CareerRequestServiceImpl implements CareerRequestService {

    @Autowired
    private CareerRequestRepository careerRequestRepository;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public boolean isRoleAlreadtAssigned(User user, CareerRequest careerRequest) {
        List<Long> allUserIds = careerRequestRepository.findAllUserIds();

        if(!allUserIds.contains(user.getId())){
            return false;
            
        }

        List<Long> request= careerRequestRepository.findByUserId(user.getId());

        return request.stream().anyMatch(roleId -> roleId.equals(careerRequest.getRole().getId()));
    }

    public void save(CareerRequest careerRequest, User user) {
        careerRequest.setUser(user);
        careerRequest.setIsChecked(false);
        careerRequestRepository.save(careerRequest);

        //Invio richiesta di ruolo all'Admin
        emailService.sendSimpleEmail("adminAulabpost@admin.com", "Richiesta per ruolo : " + careerRequest.getRole().getName(), "C'è una richiesta di collaborazione da parte di " + user.getUsername());
    }

    @Override
    public void careerAccept(Long requestId) {
        //Recupero la richiesta 
        CareerRequest request= careerRequestRepository.findById(requestId).get();

        //Dalla richiesta estraggo l'utente richiedente ed il ruolo richiesto 
        User user= request.getUser();
        Role role= request.getRole();

        //Recupero tutti i ruoli che l'utente già possiede ed aggiungo quello nuovo
        List<Role> roleUser= user.getRoles();
        Role newRole= roleRepository.findByName((role.getName()));
        roleUser.add(newRole);
        
        //Salvo tutte le modifiche
        user.setRoles(roleUser);
        userRepository.save(user);
        request.setIsChecked(true);
        careerRequestRepository.save(request);

        emailService.sendSimpleEmail(user.getEmail(), "Ruolo abilitato", "Ciao,la tua richiesta di collaborazione per il ruolo " + role.getName() + " è stata accettata dalla nostra amministrazione");
    }

    @Override
    public CareerRequest find(Long Id) {
        return careerRequestRepository.findById(Id).get();
    }

    
}
