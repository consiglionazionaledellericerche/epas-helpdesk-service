package it.cnr.iit.epas.helpdesk.service;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import lombok.Data;

@Data
public class EmailData {

  private String from = null;
  private List<String> to = Lists.newArrayList();
  private List<String> cc = Lists.newArrayList();
  private Optional<String> replyTo = Optional.empty();
  private String subject = null;
  private String body;
  private List<FileAttachment> attachments = Lists.newArrayList();

}