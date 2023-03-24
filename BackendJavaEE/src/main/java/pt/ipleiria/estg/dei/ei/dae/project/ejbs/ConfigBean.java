package pt.ipleiria.estg.dei.ei.dae.project.ejbs;

import pt.ipleiria.estg.dei.ei.dae.project.Insurance;

import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyEntityNotFoundException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;


import java.util.logging.Logger;

@Singleton
@Startup
public class ConfigBean {

    private static final Logger logger = Logger.getLogger("ejbs.ConfigBean");

    @EJB
    ClientBean cb;

    @EJB
    OccurrenceBean ob;

    @EJB
    RepairmanBean rb;

    @EJB
    DocumentBean db;

    @EJB
    ExpertBean eb;



    @PostConstruct

    public void populateDB() {

        try {
        //Insurance experts
            eb.create("expertFidelidade1", "123", "Expert 1 Fidelidade", "expert1fidelidade@mail.com", Insurance.Fidelidade);
            eb.create("expertFidelidade2", "123", "Expert 2 Fidelidade", "expert2fidelidade@mail.com", Insurance.Fidelidade);
            eb.create("expertTeleseguros1", "123", "Expert 1 Teleseguros", "expert1Teleseguros@mail.com", Insurance.Teleseguros);
            eb.create("expertTeleseguros2", "123", "Expert 2 Teleseguros", "expert2Teleseguros@mail.com", Insurance.Teleseguros);
            eb.create("expertSegurosSA1", "123", "Expert 1 SegurosSA", "expert1SegurosSA@mail.com", Insurance.SegurosSA);
            eb.create("expertSegurosSA2", "123", "Expert 2 SegurosSA", "expert2SegurosSA@mail.com", Insurance.SegurosSA);
            eb.create("expertSegurosPortugal1", "123", "Expert 1 SegurosPortugal", "expert1SegurosPortugal@mail.com", Insurance.SegurosPortugal);
            eb.create("expertSegurosPortugal2", "123", "Expert 2 SegurosPortugal", "expert2SegurosPortugal@mail.com", Insurance.SegurosPortugal);
            eb.create("expertGlobalInsurance1", "123", "Expert 1 GlobalInsurance", "expert1GlobalInsurance@mail.com", Insurance.GlobalInsurance);
            eb.create("expertGlobalInsurance2", "123", "Expert 2 GlobalInsurance", "expert2GlobalInsurance@mail.com", Insurance.GlobalInsurance);

        //Repairservices
            //Insurance repair services
            rb.create("repairServiceFidelidade", "123", "Repair Service Fidelidade", "repairServiceFidelidade@mail.com", Insurance.Fidelidade);
            rb.create("repairServiceTeleseguros", "123", "Repair Service Teleseguros", "repairServiceTeleseguros@mail.com", Insurance.Teleseguros);
            rb.create("repairServiceSegurosPortugal", "123", "Repair Service Seguros Portugal", "repairServiceSegurosPortugal@mail.com", Insurance.SegurosPortugal);
            rb.create("repairServiceSegurosSA", "123", "Repair Service SegurosSA", "repairServiceSegurosSA@mail.com", Insurance.SegurosSA);
            rb.create("repairServiceGlobalInsurance", "123", "Repair Service Global Insurance", "repairServiceGlobalInsurance@mail.com", Insurance.GlobalInsurance);

            //private repair services
            rb.create("repairServiceWorten", "123", "Repair Service Worten", "repairServiceWorten@mail.com", null);
            rb.create("repairServiceAmazon", "123", "Repair Service Amazon", "repairServiceAmazon@mail.com", null);
            rb.create("repairServicePCDiga", "123", "Repair Service PCDiga", "repairServicePCDiga@mail.com", null);
            rb.create("repairServiceVolkswagen", "123", "Repair Service Volkswagen", "repairServiceVolkswagen@mail.com", null);
        } catch (MyEntityExistsException | MyEntityNotFoundException | MyConstraintViolationException e) {
            throw new RuntimeException(e);
        }
    }
}
