package com.gis.gis.payloads.request;

import java.util.Map;

public record Report(Pagination pagination, Sort sort, String search, String startDate,
        String endDate, Map<String, Object> custom) {
};
