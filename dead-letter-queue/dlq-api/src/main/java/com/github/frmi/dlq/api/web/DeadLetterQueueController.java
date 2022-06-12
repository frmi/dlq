package com.github.frmi.dlq.api.web;

import com.github.frmi.dlq.api.service.DlqService;
import com.github.frmi.dlq.api.web.dto.DlqRecordDto;
import com.github.frmi.dlq.api.web.dto.DlqRecordDtoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DeadLetterQueueController {

    private final DlqService dlqService;

    public DeadLetterQueueController(DlqService dlqService) {
        this.dlqService = dlqService;
    }

    @Operation(summary = "Retrieve all records waiting for retry",
            parameters = {@Parameter(name = "includeRetried",
                    schema = @Schema(implementation = Boolean.class),
                    description = "By default records that has successfully been retried are not included in the " +
                            "response of this request. Use this flag to overwrite this behaviour.")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Query succeeded",
                    content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = DlqRecordDtoResponse.class))
                    )
            })
    })
    @GetMapping("/all")
    public List<DlqRecordDtoResponse> all(@RequestParam(required = false) boolean includeRetried) {
        return dlqService.getDlqRecords(includeRetried);
    }

    @Operation(summary = "Retrieve one record",
            parameters = {@Parameter(name = "id",
                    schema = @Schema(implementation = Long.class),
                    description = "Id of the record to be retrieved.")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DlqRecordDtoResponse.class)
                            )
                    }),
            @ApiResponse(responseCode = "404", description = "Record not found",
                    content = @Content)
    })
    @GetMapping("/find/{id}")
    public DlqRecordDtoResponse find(@PathVariable long id) {
        return dlqService.findById(id);
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
    public DlqRecordDtoResponse push(@RequestBody DlqRecordDto dlqRecordDto) {
        return dlqService.push(dlqRecordDto);
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
            @ApiResponse(responseCode = "400", description = "Failed to retry, or record has already been retried and 'forceRetry' flag is 'false'.",
                    content = @Content)
    })
    @GetMapping("/retry/{id}")
    public DlqRecordDtoResponse retry(@PathVariable long id, @RequestParam(required = false) boolean forceRetry) {
        return dlqService.retry(id, forceRetry);
    }
}
