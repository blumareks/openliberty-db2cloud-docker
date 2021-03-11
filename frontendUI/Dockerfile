# Start with OL runtime.
# tag::from[]
#FROM openliberty/open-liberty:kernel-java8-openj9-ubi
FROM open-liberty:20.0.0.12-full-java11-openj9


# end::from[]

ARG VERSION=1.0
ARG REVISION=SNAPSHOT
# tag::label[]

# tag::user-root[]
USER root
# end::user-root[]

# tag::copy[]
COPY --chown=1001:0 src/main/liberty/config/server.xml /config/
COPY --chown=1001:0 target/*.war /config/apps/
# end::copy[]
# tag::user[]
USER 1001
# end::user[]
