package com.github.frmi.dlq.app.web;

import com.github.frmi.dlq.app.service.DlqService;
import com.github.frmi.dlq.api.dto.DlqRecordDto;
import com.github.frmi.dlq.api.dto.DlqRecordDtoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/enqueue")
public class DlqPushController {

    private final DlqService dlqService;

    public DlqPushController(DlqService dlqService) {
        this.dlqService = dlqService;
    }
    @Operation(summary = "Push a record to the Dead Letter Queue.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
                    content = @Content(schema = @Schema(implementation = DlqRecordDto.class)),
                    description = "Record to be pushed to the Dead Letter Queue.")
            )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record successfully pushed.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DlqRecordDtoResponse.class)
                            )
                    }),
            @ApiResponse(responseCode = "400", description = "Failed to push record to Dead Letter Queue.",
                    content = @Content)
    })
    @PostMapping("/push")
    public DlqRecordDtoResponse push(@Valid @RequestBody DlqRecordDto dlqRecordDto) {
        return dlqService.push(dlqRecordDto);
    }
}
