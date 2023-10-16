db.createUser({
    user: "manager", pwd: "1234", roles:
        [{role: "readWrite", db: "application"}]
})