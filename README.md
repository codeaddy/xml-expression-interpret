# xml_expression_interpret

Универсальный интерпретатор XML-выражений с XPath

Выражения могут содержать как простые запросы к элементам/атрибутам XML,
поддерживаемые базовыми средствами Java для вычисления XPath,
так и сложные логические конструкции вида:

``` 
if (выражение1) then (выражение2) else (выражение3)
```

Например:

```
if (exists(//xbrli:period/xbrli:startDate)) then max(//xbrli:period/xbrli:startDate) else xsd:date('2016-01-01')
```