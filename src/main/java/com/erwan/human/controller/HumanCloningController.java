package com.erwan.human.controller;

import com.erwan.human.dao.CloneRepository;
import com.erwan.human.domaine.Clone;
import com.erwan.human.exceptions.BeanNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/kamino/clones")
public class HumanCloningController {

    @Autowired
    private CloneRepository repository;

    @GetMapping("/")
    @PreAuthorize("hasAnyAuthority('ROLE_KAMINOAIN', 'ROLE_EMPEROR')")
    public List<Clone> findAll() {
        return repository.findAll();
    }

    @GetMapping("/pages")
    @PreAuthorize("hasAnyAuthority('ROLE_KAMINOAIN', 'ROLE_EMPEROR')")
    public Page<Clone> findAllPages(@PageableDefault(page = 0, size = 20)
                                        @SortDefault.SortDefaults({
                                                @SortDefault(sort = "id", direction = Sort.Direction.ASC)
                                        }) Pageable pageable) {
        return repository.findAll(pageable);
    }

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_KAMINOAIN', 'ROLE_EMPEROR')")
    public Clone createClone(@RequestBody Clone clone){
        return repository.save(clone);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_KAMINOAIN', 'ROLE_EMPEROR')")
    public Clone findById(@PathVariable("id") Long id) throws BeanNotFound {
        return getOne(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_KAMINOAIN', 'ROLE_EMPEROR')")
    public void delete(@PathVariable("id") Long id) throws BeanNotFound {
        Clone clone = getOne(id);
        repository.delete(clone);
    }

    @PutMapping("/order66")
    @PreAuthorize("hasAuthority('ROLE_EMPEROR')")
    public List<Clone> executeOrder66(){
        List<Clone> clones = repository.findAll();
        clones.stream().forEach(clone -> clone.setAffiliation("Galactic Empire"));
        return repository.saveAll(clones);
    }

    protected Clone getOne(Long id) throws BeanNotFound {
        Optional<Clone> clone = repository.findById(id);
        if(!clone.isPresent()){
            throw new BeanNotFound("Can't find clone with id : " + id);
        }
        return clone.get();
    }

}