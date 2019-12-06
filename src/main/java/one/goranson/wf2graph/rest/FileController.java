package one.goranson.wf2graph.rest;

import lombok.extern.slf4j.Slf4j;
import one.goranson.wf2graph.exceptions.ValidationException;
import one.goranson.wf2graph.service.FileService;
import one.goranson.wf2graph.service.ParserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Objects;

@Controller
@Slf4j
public class FileController {

    private FileService fileService;
    private ParserService parserService;

    public FileController(FileService fileService, ParserService parserService) {
        this.fileService = fileService;
        this.parserService = parserService;
    }

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException, ValidationException {

        if(Objects.isNull(file) || file.isEmpty()) {
            throw new ValidationException("File is mandatory");
        }
        parserService.generateGraph(fileService.uploadFile(file));
        redirectAttributes.addFlashAttribute("message",
                "The file ".concat(file.getOriginalFilename()).concat(" has now been imported to the Graph database!"));
        return "redirect:/";
    }
}