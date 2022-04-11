# TransactionalStore
There is application for Transactional Key Value Store. It is implemented with MVVP & Clean Architcture.

You have 7 available operation's.
1. **SET** _key_ _value_ -- store value with specified key
2. **GET** _key_ -- get stored value for this given key
3. **DELETE** _key_ -- delete entry with specified key
4. **COUNT** _value_ -- return the number of keys that have the given value
5. **BEGIN** -- start a new transaction
6. **COMMIT** -- complete transaction's ans permanently save all data
7. **ROLLBACK** -- revert state to prior previous **BEGIN** call 

If data was commited you can't rollback it.
