/*
 * Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package it.cnr.iit.epas.helpdesk.service;

import com.google.common.base.Verify;
import it.cnr.iit.epas.helpdesk.dto.v4.ReportData;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import lombok.val;

/**
 * Utility per la creazione dei file compressi con il codice Html ed alcune
 * informazioni di debug.
 *
 * @author Cristian Lucchesi
 *
 */
public class FileUtils {
  //application/octet-stream

  public static byte[] gzipText(String text) throws IOException {
    Verify.verifyNotNull(text);
    ByteArrayOutputStream htmlGz = new ByteArrayOutputStream();
    GZIPOutputStream gz = new GZIPOutputStream(htmlGz);
    gz.write(text.getBytes());
    gz.close();
    return htmlGz.toByteArray();
  }

  public static byte[] debugFile(ReportData reportData) 
      throws IOException {
    Verify.verifyNotNull(reportData);

    StringBuffer buffer = new StringBuffer();
    buffer.append(String.format("UserAgent: %s\n\n", reportData.getBrowser().getUserAgent()));
    if (reportData.getSession() != null) {
      for (val entry : reportData.getSession().entrySet()) {
        buffer.append(String.format("%s: %s\n", entry.getKey(), entry.getValue()));
      }
    }
    return gzipText(buffer.toString());
  }
}