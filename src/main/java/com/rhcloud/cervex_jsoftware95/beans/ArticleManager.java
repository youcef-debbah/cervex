/*
 * Copyright (c) 2018 youcef debbah (youcef-kun@hotmail.fr)
 *
 * This file is part of cervex.
 *
 * cervex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cervex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cervex.  If not, see <http://www.gnu.org/licenses/>.
 *
 * created on 2018/12/08
 * @header
 */

package com.rhcloud.cervex_jsoftware95.beans;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import com.rhcloud.cervex_jsoftware95.entities.Article;
import com.rhcloud.cervex_jsoftware95.entities.Demand;
import com.rhcloud.cervex_jsoftware95.entities.File;
import com.rhcloud.cervex_jsoftware95.exceptions.InformationSystemException;

/**
 * 
 * @author youcef debbah
 */
@Local
public interface ArticleManager extends FileManager {

    // articles managing
    public List<Article> getAllArticles();

    public void addArticle(String username, String demandID, String version, int progress);

    public Article getArticle(String articleID);
    
    /**
     * 		This methode DELETE Article that his ID was given
     * @param articleID 
     * 		  The ID of Article you want to delete
     * @throws CanNotDeleteArticle
     * 		   When you can't delete Article for some reason 
     */
    public void deleteArticle(String articleID);

    public void updateArticle(String articleID, String version, int progress);

    // demands managing
    public List<Demand> getAllDemands();

    public void sendDemand(String title, String type, String description, Collection<File> files);

    public Demand getDemand(String demandID);
    /**
     * 		This methode DELETE Demand that his ID was given
     * 		and its article and files if it was found 
     * @param demandID
     * 		  The ID of Demand you want to delete
     * @throws NullArgumentException
     * 			When demandID is Null
     * @throws CanNotDeleteArticle
     * 			When you can't delete article tha attached to this demand for some reason
     * @throws InformationSystemException
     * 			When one of Article or files not found or already deleted
     * @throws CanNotDeleteDemand
     * 		   When you can't delete Demand for some reason 
     */
    public boolean deleteDemand(String demandID);
    
    public void deleteDemand(String demandID,boolean canDelete);

    public Map<String, Long> getDemandsStatics();

	public List<Demand> getDemands(String[] demands);

	public List<Article> getArticles(String[] articles);

	public void markAsOldDemand(String demand);

	public void markAsOldArticle(String article);

}
