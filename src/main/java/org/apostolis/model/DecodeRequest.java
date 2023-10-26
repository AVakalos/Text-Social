package org.apostolis.model;

import org.hibernate.validator.constraints.URL;

public record DecodeRequest(@URL String url){ }
