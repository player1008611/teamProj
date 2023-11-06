package com.teamProj.entity;

public class Enterprise {
    String enterpriseId;
    String enterpriseName;
    String website;

    @Override
    public String toString() {
        return "enterprise{" +
                "enterpriseId='" + enterpriseId + '\'' +
                ", enterpriseName='" + enterpriseName + '\'' +
                ", website='" + website + '\'' +
                '}';
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
