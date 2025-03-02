# GCP_Signing_Headers_Test
This code is an app to generate GCP Cloud Storage (GCS) signed URLs with extra headers to see if any will make it to the GCS logs for correlation (i.e., signed URL link to GCS log tracing) &amp; tracking (e.g., to limit the usable period of signing key to a single download / minimal time period).


## Example of GCS Audit Log entry with extended query parameters & non-"audit" extended headers (extended headers that don't start with "x-goog-custom-audit-<key>").

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
### See "audit_info" key below

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

