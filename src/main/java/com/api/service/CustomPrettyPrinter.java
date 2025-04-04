package com.api.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import java.io.IOException;

public class CustomPrettyPrinter extends DefaultPrettyPrinter {
    public CustomPrettyPrinter() {
        _arrayIndenter = new DefaultIndenter("    ", "\n");
        _objectIndenter = new DefaultIndenter("    ", "\n");
    }

    @Override
    public DefaultPrettyPrinter createInstance() {
        return new CustomPrettyPrinter();
    }

    @Override
    public void writeStartArray(JsonGenerator g) throws IOException {
        g.writeRaw("[\n    ");
    }

    @Override
    public void writeEndArray(JsonGenerator g, int nrOfValues) throws IOException {
        g.writeRaw("\n]");
    }
}