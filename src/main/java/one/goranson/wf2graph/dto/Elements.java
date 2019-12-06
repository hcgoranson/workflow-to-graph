package one.goranson.wf2graph.dto;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Value
@Slf4j
public class Elements {
    private final Map<String, List<Element>> groupedElement;
    public Elements(Map<String, List<Element>> groupedElement) {
        this.groupedElement = groupedElement;
    }
}
