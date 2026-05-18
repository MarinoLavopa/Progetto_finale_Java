package it.aulab.progetto_finale_java.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.aulab.progetto_finale_java.models.CareerRequest;
import it.aulab.progetto_finale_java.models.User;
import it.aulab.progetto_finale_java.repositories.CareerRequestRepository;
import jakarta.transaction.Transactional;

@Service
public class CareerRequestServiceImpl implements CareerRequestService {

    @Autowired
    private CareerRequestRepository careerRequestRepository;

    @Autowired
    private EmailServiceImpl emailService;

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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'careerAccept'");
    }

    @Override
    public CareerRequest find(Long Id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'find'");
    }

    
}
