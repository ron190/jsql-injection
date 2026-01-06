package spring.rest;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController
// no propagation to prevent: Transaction silently rolled back because it has been marked as rollback-only
@Transactional(propagation = Propagation.NEVER)
@RequestMapping("/tx")
public class JpaStackRestController extends RestController {

    @RequestMapping("/stack")
    public Greeting endpointStack(@RequestParam(value="name", defaultValue="World") String name, @RequestHeader Map<String, String> headers) {
        if (name.toLowerCase().contains("union")) {
            return null;
        }
        return this.getResponse(name, "select First_Name from Student where '1' = '%s'", false, false, true, false, false, true);
    }

    @RequestMapping("/stack2")
    public Greeting endpointStack2(@RequestParam(value="name", defaultValue="World") String name, @RequestParam(value="semicolon", defaultValue="true") String semicolon, @RequestHeader Map<String, String> headers) {
        Greeting greeting = new Greeting(null);
        var result = new StringBuilder();
        Arrays.stream(("select First_Name from Student where '1' = '"+name+"'").split(";")).map(String::trim).forEach(query -> {
            if ("true".equals(semicolon)) {
                query = query +";";
            }
            Greeting g = this.getResponse(query, query, true, false, true, false, false, true);
            if (g != null) result.append(g.getContent());
        });
        greeting.setContent(result.toString());
        return greeting;
    }

    @RequestMapping("/update")
    public Greeting endpointUpdatea(@RequestParam(value="name", defaultValue="World") String name) {
        return this.getResponse(name, "update StudentForDelete set Class_Name = '' where 'not_found' = '%s'", true, true, false);
    }

    @RequestMapping("/delete")
    public Greeting endpointDelete(@RequestParam(value="name", defaultValue="World") String name) {
        return this.getResponse(name, "delete from StudentForDelete where 'not_found' = '%s'", true, true, false);
    }

    @RequestMapping("/insert")
    public Greeting endpointInsert(@RequestParam(value="name", defaultValue="World") String name) {
        return this.getResponse(name, "insert into StudentForDelete select * from StudentForDelete where 'not_found' = '%s'", true, true, false);
    }
}