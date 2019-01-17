#!/usr/bin/env bash
echo "Adding users to DTU-PAY MongoDB..."
mongo dtupay --host localhost -u root -p rootPassXXX --authenticationDatabase admin --eval '
db.accounts.insert( { className : "dtu.dtupay.model.Account", name: "Fredrik", role: "User"} );
'
echo "Users added."