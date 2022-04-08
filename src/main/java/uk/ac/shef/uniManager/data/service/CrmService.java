package uk.ac.shef.uniManager.data.service;
import org.springframework.stereotype.Service;
import uk.ac.shef.uniManager.data.entity.Company;
import uk.ac.shef.uniManager.data.entity.Contact;
import uk.ac.shef.uniManager.data.entity.Status;
import uk.ac.shef.uniManager.data.repository.CompanyRepository;
import uk.ac.shef.uniManager.data.repository.ContactRepository;
import uk.ac.shef.uniManager.data.repository.StatusRepository;

import java.util.List;

@Service 
public class CrmService {

    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;
    private final StatusRepository statusRepository;

    public CrmService(ContactRepository contactRepository,
                      CompanyRepository companyRepository,
                      StatusRepository statusRepository) { 
        this.contactRepository = contactRepository;
        this.companyRepository = companyRepository;
        this.statusRepository = statusRepository;
    }

    public List<Contact> findAllContacts(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) { 
            return contactRepository.findAll();
        } else {
            return contactRepository.search(stringFilter);
        }
    }

    public long countContacts() {
        return contactRepository.count();
    }

    public void deleteContact(Contact contact) {
        contactRepository.delete(contact);
    }

    public void saveContact(Contact contact) {
        if (contact == null) { 
            System.err.println("Contact is null. Are you sure you have connected your form to the application?");
            return;
        }
        contactRepository.save(contact);
    }

    public List<Company> findAllCompanies() {
        return companyRepository.findAll();
    }

    public List<Status> findAllStatuses(){
        return statusRepository.findAll();
    }
}