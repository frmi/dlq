package com.github.frmi.dlq.app.web;

import com.github.frmi.dlq.api.dto.DlqRecordDtoResponse;
import com.github.frmi.dlq.app.service.DlqService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/retry")
public class DlqRetryController {

    private final DlqService dlqService;

    public DlqRetryController(DlqService dlqService) {
        this.dlqService = dlqService;
    }

    @Operation(summary = "Retry a record.",
            parameters = {@Parameter(name = "id",
                    schema = @Schema(implementation = Long.class),
                    description = "Id of the record to be retried."),
                    @Parameter(name = "forceRetry",
                            schema = @Schema(implementation = Boolean.class),
                            description = "Forces retry if the record has already been retried.")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record successfully retried.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DlqRecordDtoResponse.class)
                            )
                    }),
            @ApiResponse(responseCode = "404", description = "Record not found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Failed to retry.",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public DlqRecordDtoResponse retry(@PathVariable long id) {
        return dlqService.retry(id);
    }
}
