package contracts;

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return all profiles"

    request {
        url "/profiles"
        method GET()
    }

    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        // body '''
        //     {
        //         [
        //             "id": 1,
        //             "name": "Joseph",
        //             "age": 22,
        //             "email": jose@gmail.com,
        //             "dob": "2000-01-01"
        //         ]
        //     }
        // '''
        body (
            [
                [
                id: 1,
                name: "Joseph",
                age: 22,
                email: "jose@gmail.com",
                // dob: "2000-1-1"
                ],
                [
                    id: 2,
                    name: "Sam",
                    age: 32,
                    email: "sam@gmail.com",
                    // dob: "2000-1-1"
                ]
            ]

        )
    }
}