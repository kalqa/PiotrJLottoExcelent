db.createUser(
    {
        user: "kalqa",
        pwd: "kalqa",
        roles: [
            {
                role: "readWrite",
                db: "tickets"
            }
        ]
    }
)
