<?xml version="1.0" encoding="UTF-8"?>
<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron">
    <sch:pattern name="structure checks">
        <sch:rule context="//dogs/dog">
            <sch:assert id="check-sex-is-defined" test="@sex">A dog has to have the 'sex' attribute.</sch:assert>
        </sch:rule>
    </sch:pattern>
</sch:schema>
