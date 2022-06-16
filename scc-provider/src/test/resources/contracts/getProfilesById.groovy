package contracts;

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return profile when id = 1"

    request {
        url "/profiles/1"
        method GET()
    }

    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body (
            id: 1,
            name: "Joseph",
            age: 22,
            email: "jose@gmail.com",
            // dob: "2000-1-1"
        )
    }
}