//
// typica - A client library for Amazon Web Services
// Copyright (C) 2007 Xerox Corporation
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package com.xerox.amazonws.ec2;

/**
 * This enumeration represents different instance types that can be launched.
 */
public enum InstanceType {
	DEFAULT ("m1.small"),
	MICRO ("t1.micro"),
	LARGE ("m1.large"),
	XLARGE ("m1.xlarge"),
	MEDIUM_HCPU ("c1.medium"),
	XLARGE_HCPU ("c1.xlarge"),
	XLARGE_HMEM ("m2.xlarge"),
	XLARGE_DOUBLE_HMEM ("m2.2xlarge"),
	XLARGE_QUAD_HMEM ("m2.4xlarge"),
	XLARGE_CLUSTER_COMPUTE ("cc1.4xlarge");
	
	private final String typeId;

	InstanceType(String typeId) {
		this.typeId = typeId;
	}

	public String getTypeId() {
		return typeId;
	}

	public static InstanceType getTypeFromString(String val) {
		for (InstanceType t : InstanceType.values()) {
			if (t.getTypeId().equals(val)) {
				return t;
			}
		}
		return null;
	}
}
