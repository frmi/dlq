package com.github.frmi.dlq.api.service;

import com.github.frmi.dlq.api.data.DlqRecord;

public interface DlqRetryer {

    boolean retry(DlqRecord record);

}
