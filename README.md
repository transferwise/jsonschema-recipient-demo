# JSON schema recipient demo
A small demo to show how JSON schema can be parsed by jackson and rendered recursively.

Expected output:
```
-------allOf------
    string: currency
    array: [
        string: firstName
        string: lastName
    ]
  -------oneOf------
      string: fullName
      string: sortCode
      string: accountNumber
    -------or------
      string: iban
  -------end oneOf--
    string: streetAddress
-------end allOf--
```

The indentation demonstartes the nesting of the JSON schema
