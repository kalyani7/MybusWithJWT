package com.mybus.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 
 * @author yks-Srinu
 *
 */

public class EmailData {
	@Setter
	@Getter
	private String from; 
	@Setter
	@Getter
	private List<String> to;
	@Setter
	@Getter
	private List<String> cc;
	@Setter
	@Getter
	private List<String> bcc;
	@Setter
	@Getter
	private String subject;
	@Setter
	@Getter
	private String body;
	@Setter
	@Getter
	private byte[] attachmentAsByteArrResource;
	
	public static Builder getBuilder() {
		return new Builder();
	}
	public static class Builder {

		private final EmailData built;
		
		public Builder() {
			this.built 	= new EmailData();
		}
		public Builder from(final String fromEmailId) {
			this.built.from = fromEmailId;
			return this;
		}
		
		public Builder to(final List<String> toEmailIds) {
			this.built.to = toEmailIds;
			return this;
		}
		public Builder cc(final List<String> ccEmailIds) {
			this.built.cc = ccEmailIds;
			return this;
		}
		public Builder bcc(final List<String> bccEmailIds) {
			this.built.bcc = bccEmailIds;
			return this;
		}
		public Builder attachmentAsByteArrResource(final byte[] attachmentAsByteArrResource) {
			this.built.attachmentAsByteArrResource = attachmentAsByteArrResource;
			return this;
		}
		public Builder body(final String body) {
			this.built.body = body;
			return this;
		}
		public Builder subject(final String subject) {
			this.built.subject = subject;
			return this;
		}
		public EmailData build(){
			return built;
		}
	}
}
