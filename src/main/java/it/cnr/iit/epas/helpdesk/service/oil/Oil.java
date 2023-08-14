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

package it.cnr.iit.epas.helpdesk.service.oil;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.form.FormData;

@Headers({"Content-Type: application/json"})
public interface Oil {

    @RequestLine("PUT /rest/pest/{instance}")
    public Long newProblem(OilCreateDto problem, @Param("instance")String instance);

    @RequestLine("POST /rest/pest/{instance}")
    public Long addField(OilCreateDto problem, @Param("instance")String instance);

    @RequestLine("POST /rest/pest/{instance}/{id}")
    @Headers("Content-Type: multipart/form-data")
    public Long addAttachment(@Param("id")Long id, @Param("allegato")FormData formData, 
        @Param("instance")String instance);

}