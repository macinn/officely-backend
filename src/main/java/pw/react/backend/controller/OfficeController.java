package pw.react.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pw.react.backend.services.OfficeService;

@RestController
@RequestMapping(path = UserController.USERS_PATH)
public class OfficeController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    static final String USERS_PATH = "/offices";
    private final OfficeService officeService;
    public OfficeController(OfficeService officeService) {
        this.officeService = officeService;
    }

}
