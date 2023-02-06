package com.github.frmi.dlq.api.web;

import com.github.frmi.dlq.api.service.DlqService;
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
@RequestMapping("/view")
public class DlqViewController {

    private final DlqService dlqService;

    public DlqViewController(DlqService dlqService) {
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

}
