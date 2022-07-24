package contracts;

import org.springframework.cloud.contract.spec.Contract

// 1. Declare all different contract within this \contract package
// running `generateContractTests` task will generate the various test method in `ContractVerifierTest`
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
        // Different ways to pass in body
        // textblock style
        // body '''\
        //         [
        //             {
        //                 "id": 1,
        //                 "name": "Joseph",
        //                 "age": 22,
        //                 "email": jose@gmail.com,
        //                 "dob": "2000-01-01"
        //             },
        //             {
        //                 "id": 2,
        //                 "name": "Sam",
        //                 "age": 32,
        //                 "email": sam@gmail.com,
        //                 "dob": "2000-01-01"
        //             }
        //         ]
        //     '''

        // array style
        // body (
        //     [
        //         [
        //             id: 1,
        //             name: "Joseph",
        //             age: 22,
        //             email: "jose@gmail.com",
        //             dob: "2000-01-01"
        //         ],
        //         [
        //             id: 2,
        //             name: "Sam",
        //             age: 32,
        //             email: "sam@gmail.com",
        //             dob: "2000-01-01"
        //         ]
        //     ]
        // )

        // with a file
        body(file("getAllProfiles_Response.json"))
    }
}