package com.ruoyi.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.DistributionConfig;
import com.ruoyi.system.service.IDistributionConfigService;

/**
 * 分销配置Controller
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
@RestController
@RequestMapping("/system/distributionConfig")
public class DistributionConfigController extends BaseController
{
    @Autowired
    private IDistributionConfigService distributionConfigService;

    /**
     * 查询分销配置列表
     */
    @PreAuthorize("@ss.hasPermi('system:distributionConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(DistributionConfig distributionConfig)
    {
        startPage();
        List<DistributionConfig> list = distributionConfigService.selectDistributionConfigList(distributionConfig);
        return getDataTable(list);
    }

    /**
     * 导出分销配置列表
     */
    @PreAuthorize("@ss.hasPermi('system:distributionConfig:export')")
    @Log(title = "分销配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DistributionConfig distributionConfig)
    {
        List<DistributionConfig> list = distributionConfigService.selectDistributionConfigList(distributionConfig);
        ExcelUtil<DistributionConfig> util = new ExcelUtil<DistributionConfig>(DistributionConfig.class);
        util.exportExcel(response, list, "分销配置数据");
    }

    /**
     * 获取分销配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:distributionConfig:query')")
    @GetMapping(value = "/{configId}")
    public AjaxResult getInfo(@PathVariable("configId") Long configId)
    {
        return success(distributionConfigService.selectDistributionConfigByConfigId(configId));
    }

    /**
     * 新增分销配置
     */
    @PreAuthorize("@ss.hasPermi('system:distributionConfig:add')")
    @Log(title = "分销配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DistributionConfig distributionConfig)
    {
        return toAjax(distributionConfigService.insertDistributionConfig(distributionConfig));
    }

    /**
     * 修改分销配置
     */
    @PreAuthorize("@ss.hasPermi('system:distributionConfig:edit')")
    @Log(title = "分销配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DistributionConfig distributionConfig)
    {
        return toAjax(distributionConfigService.updateDistributionConfig(distributionConfig));
    }

    /**
     * 删除分销配置
     */
    @PreAuthorize("@ss.hasPermi('system:distributionConfig:remove')")
    @Log(title = "分销配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Long[] configIds)
    {
        return toAjax(distributionConfigService.deleteDistributionConfigByConfigIds(configIds));
    }
}
