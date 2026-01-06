package spring.rest;

import org.springframework.transaction.annotation.Transactional;

@org.springframework.web.bind.annotation.RestController
public class JpaRestController extends RestController {

    @Transactional  // non-stack not working with silent rollback when annotation on class
    protected Greeting getResponse(
        String name,
        String sqlQuery,
        boolean isError,
        boolean isUpdate,
        boolean isVisible,
        boolean isOracle,
        boolean isBoolean,
        boolean isStack
    ) {
        return super.getResponse(name, sqlQuery, isError, isUpdate, isVisible, isOracle, isBoolean, isStack);
    }
}