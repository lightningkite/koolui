package com.larvalabs.svgandroid

/**
 * Runtime exception thrown when there is a problem parsing an SVG.
 *
 * @author Larva Labs, LLC
 */
class SVGParseException : RuntimeException {

    constructor(s: String) : super(s) {}

    constructor(s: String, throwable: Throwable) : super(s, throwable) {}

    constructor(throwable: Throwable) : super(throwable) {}
}
