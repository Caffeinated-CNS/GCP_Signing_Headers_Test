# GCP_Signing_Headers_Test
This code is an app to generate GCP Cloud Storage (GCS) signed URLs with extra headers to see if any will make it to the GCS logs for correlation (i.e., signed URL link to GCS log tracing) &amp; tracking (e.g., to limit the usable period of signing key to a single download / minimal time period).



## Example of GCS Audit Log entry with extended query parameters & non-"audit" extended headers (extended headers that don't start with "x-goog-custom-audit-<key>").

Example URL

```
curl 
	-H "X-Goog-Log-Me: header not included in logging" 
	"https://storage.googleapis.com/<GCS Bucket>/<object name>
		?Consumer-Name=client_asdasdasd_1234
		&Extra-Log-Info=some%20thing%20for%20query%20log%20discovery
		&Generator-Service=com.test.gcp.GCS_Signed_URL_With_Addtional_Headers%2F0.1
		&X-Goog-Algorithm=GOOG4-RSA-SHA256
		&X-Goog-Credential=sa-gcs-reader%40<project id>.iam.gserviceaccount.com%2F20250303%2Fauto%2Fstorage%2Fgoog4_request
		&X-Goog-Date=20250303T025037Z&X-Goog-Expires=900
		&X-Goog-SignedHeaders=host%3Bx-goog-log-me
		&X-Goog-Signature=24423a77ca297de1e1996c2a0dd7c9292d413599943265e999b0ff4f7a8633bec3f9be570a16b9a12d8b89e25201e0d0cfedc097214e666b060b0d928d49b1063acd1932ddb9999faeb05927b626f1287f02483f557f0eb8db0568aa069683999f0aeb07796268ac20ce123a989593ef8e839ed7993b71afeb1178aaf52578cca4d075d4026a67b381a0bbeddd5c29025fdee840b19435accc7f47f43648ea0bf7ed3999954c4f1d0e2593fb8b2632eac039946551fdf9ab4605082dc8dba9bc1cb533651383c9fd1088e8fdf810fc4d721a7381f8e1b8ea852ac79a2a92d0ace52e5d9abe6ba0d4a85adf769103fdb3f89f4f46c2300913a5419999b19fcf40"

```

```json
{
  "protoPayload": {
    "@type": "type.googleapis.com/google.cloud.audit.AuditLog",
    "status": {},
    "authenticationInfo": {
      "principalEmail": "<SA Used for Signing URL ID>@<project id>.iam.gserviceaccount.com"
    },
    "requestMetadata": {
      "callerIp": "<internet ip>",
      "callerSuppliedUserAgent": "curl/8.0.1,gzip(gfe)",
      "requestAttributes": {
        "time": "2025-03-02T19:08:53.376855142Z",
        "auth": {}
      },
      "destinationAttributes": {}
    },
    "serviceName": "storage.googleapis.com",
    "methodName": "storage.objects.get",
    "authorizationInfo": [
      {
        "resource": "projects/_/buckets/<GCS Bucket>/objects/<object name>",
        "permission": "storage.objects.get",
        "granted": true,
        "resourceAttributes": {}
      }
    ],
    "resourceName": "projects/_/buckets/<GCS Bucket>/objects/<object name>",
    "resourceLocation": {
      "currentLocations": [
        "us"
      ]
    }
  },
  "insertId": "9783tme621wi",
  "resource": {
    "type": "gcs_bucket",
    "labels": {
      "bucket_name": "<GCS Bucket>",
      "location": "us",
      "project_id": "<project id>"
    }
  },
  "timestamp": "2025-03-02T19:08:53.368384875Z",
  "severity": "INFO",
  "logName": "projects/<project id>/logs/cloudaudit.googleapis.com%2Fdata_access",
  "receiveTimestamp": "2025-03-02T19:08:54.196672959Z"
}
```

## Example of GCS Audit Log entry with extended query parameters + non-"audit" extended headers + "audit" extended headers (starting with "x-goog-custom-audit-<key>").
See "audit_info" key below
Note the non-audit headers are not included in the GCS audit log entries.

Example URL

```
curl 
	-H "x-goog-custom-audit-source: com.test.gcp.GCS_Signed_URL_With_Addtional_Headers/0.1" 
	-H "x-goog-custom-audit-signed-by: sa-gcs-reader@<project id>.iam.gserviceaccount.com" 
	-H "X-Goog-Log-Me: header not included in logging" 
	-H "x-goog-custom-audit-correlation-id: c3ce12fc-0ce5-4ff4-95c6-fee9b466a71b" 
	"https://storage.googleapis.com/<GCS Bucket>/<object name>
	?Consumer-Name=client_asdasdasd_1234
	&Extra-Log-Info=some%20thing%20for%20query%20log%20discovery
	&Generator-Service=com.test.gcp.GCS_Signed_URL_With_Addtional_Headers%2F0.1
	&X-Goog-Algorithm=GOOG4-RSA-SHA256
	&X-Goog-Credential=sa-gcs-reader%40<project id>.iam.gserviceaccount.com%2F20250303%2Fauto%2Fstorage%2Fgoog4_request
	&X-Goog-Date=20250303T025638Z
	&X-Goog-Expires=900
	&X-Goog-SignedHeaders=host%3Bx-goog-custom-audit-correlation-id%3Bx-goog-custom-audit-signed-by%3Bx-goog-custom-audit-source%3Bx-goog-log-me
	&X-Goog-Signature=87db3dd99915213c7a2722408cdff8471a34f105308d258a24b6ef780741943bdb8f98215179d3ddc24999058e5968d0b7dd7db31ae8e60fb954665f4b046d5ab37c0191667e3f4b0b9f7b83643fa3aac6b2252c1fa43f059beb09fd25de3999971b46ca75d9f4b3f0431ee9cfbdd04c483f3ef23485d6ecfc94d541dd781f5147fb182b97912ba1d78781150ebb9985cc428def024b83e80e3f02268232bfa85a21623f1d753563ec7b19105d95e95c2a08b59419999490a697d94bb09e29d0ead0588d309770dea3d438ee061c847a3536bd5a542fc222919c5f74a2d09e5ecd891d0422c480702338d89b09de3c09999eb6afa4c93a0663aa82cf5cb61238"


```

```json
{
  "protoPayload": {
    "@type": "type.googleapis.com/google.cloud.audit.AuditLog",
    "status": {},
    "authenticationInfo": {
      "principalEmail": "<SA Used for Signing URL ID>@<project id>.iam.gserviceaccount.com"
    },
    "requestMetadata": {
      "callerIp": "97.118.214.236",
      "callerSuppliedUserAgent": "curl/8.0.1,gzip(gfe)",
      "requestAttributes": {
        "time": "2025-03-02T19:36:56.569199590Z",
        "auth": {}
      },
      "destinationAttributes": {}
    },
    "serviceName": "storage.googleapis.com",
    "methodName": "storage.objects.get",
    "authorizationInfo": [
      {
        "resource": "projects/_/buckets/<GCS Bucket>/objects/<object name>",
        "permission": "storage.objects.get",
        "granted": true,
        "resourceAttributes": {}
      }
    ],
    "resourceName": "projects/_/buckets/<GCS Bucket>/objects/<object name>",
    "metadata": {
      "audit_context": {
        "app_context": "EXTERNAL",
        "audit_info": {
          "x-goog-custom-audit-signed-by": "<SA Used for Signing URL ID>@<project id>.iam.gserviceaccount.com",
          "x-goog-custom-audit-source": "com.test.gcp.GCS_Signed_URL_With_Addtional_Headers/0.1",
          "x-goog-custom-audit-correlation-id": "c3ce12fc-0ce5-4ff4-95c6-fee9b466a71b"
        }
      }
    },
    "resourceLocation": {
      "currentLocations": [
        "us"
      ]
    }
  },
  "insertId": "usy6duen7aix",
  "resource": {
    "type": "gcs_bucket",
    "labels": {
      "bucket_name": "<GCS Bucket>",
      "location": "us",
      "project_id": "<project id>"
    }
  },
  "timestamp": "2025-03-02T19:36:56.560018840Z",
  "severity": "INFO",
  "logName": "projects/<project id>/logs/cloudaudit.googleapis.com%2Fdata_access",
  "receiveTimestamp": "2025-03-02T19:36:56.898328697Z"
}

```

Specifically interesting & set via the code in this project:
 
```json
    "metadata": {
      "audit_context": {
        "app_context": "EXTERNAL",
        "audit_info": {
          "x-goog-custom-audit-signed-by": "<SA Used for Signing URL ID>@<project id>.iam.gserviceaccount.com",
          "x-goog-custom-audit-source": "com.test.gcp.GCS_Signed_URL_With_Addtional_Headers/0.1",
          "x-goog-custom-audit-correlation-id": "c3ce12fc-0ce5-4ff4-95c6-fee9b466a71b"
        }
      }
    }
```

<hr>

Setting a log router to load messages with the following filter to a Pub/Sub topic generates a message similar to:

Log Router 'Inclusion filter':

```
resource.type="gcs_bucket"
protoPayload.methodName="storage.objects.get"
protoPayload.serviceName="storage.googleapis.com"
```

See [GCP_CRF_Print_GCS_Audit_Log_Entries](https://github.com/Caffeinated-CNS/GCP_CRF_Print_GCS_Audit_Log_Entries) repo for a Cloud Run Functions app that will print the headers included in the above URL example & setup requirements.
